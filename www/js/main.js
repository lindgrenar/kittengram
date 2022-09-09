import KittenPost from "./KittenPost.js"
let postDiv = document.querySelector('.kitten-posts')
let addButton = document.querySelector('.add-button')
let closeButton = document.querySelector('.close-button')
let addPostForm = document.querySelector('.add-post-form')
let addPostFormImage = document.querySelector('.add-post-form > input[type="file"]')

let kittenPosts = []

document.querySelector('.logo-text').onclick = () => {
  window.scrollTo(0, 0)
  getKittenPosts()
}

getKittenPosts()
async function getKittenPosts() {
  let res = await fetch('/rest/kittens')
  res = await res.json()
  kittenPosts = res.map(post => new KittenPost(post))

  renderKittenPosts()
}

function renderKittenPosts() {
  postDiv.innerHTML = kittenPosts.map(post => post.render()).join('')
}

async function likeKittenPost(id) {
  const post = kittenPosts.find(p => p.id == id)
  await post.like()
  const card = postDiv.querySelector(`[data-id="${id}"]`)
  card.outerHTML = post.render()
}

async function unlikeKittenPost(id) {
  const post = kittenPosts.find(p => p.id == id)
  await post.unlike()
  const card = postDiv.querySelector(`[data-id="${id}"]`)
  card.outerHTML = post.render()
}

postDiv.addEventListener('click', e => {
  const likeIcon = e.target.closest('.icon-bar > .bx-heart')
  const unlikeIcon = e.target.closest('.icon-bar > .bxs-heart')
  
  if (!likeIcon && !unlikeIcon) return
  
  const card = e.target.closest('.card')
  const id = card.dataset.id

  likeIcon && likeKittenPost(id)
  unlikeIcon && unlikeKittenPost(id)
})

function openForm() {
  addButton.style.display = 'none'
  closeButton.style.display = 'block'
  addPostForm.style.setProperty('transform', 'translateY(0%)')
}

function closeForm() {
  addButton.style.display = 'block'
  closeButton.style.display = 'none'
  addPostForm.style.setProperty('transform', 'translateY(-100%)')
}

document.addEventListener('click', e => {
  if (e.target.closest('.add-button')) {
    openForm()
  }
  else if (e.target.closest('.close-button')) {
    closeForm()
  }
})

addPostForm.addEventListener('reset', e => {
  addPostForm.querySelector('img').src = "/assets/no-image.svg"
  closeForm()
})

addPostFormImage.addEventListener('input', e => {
  const [file] = e.target.files
  addPostForm.querySelector('img').src = URL.createObjectURL(file)
})

addPostForm.addEventListener('submit', async e => {
  e.preventDefault()
  uploadPost()
  addPostForm.querySelector('button[type="reset"]').click()
})

async function uploadPost() {
  const [file] = addPostFormImage.files
  let formData = new FormData()

  formData.append('file', file, file.name)
  formData.append('title', addPostForm.querySelector('[placeholder="title"]').value)
  formData.append('description', addPostForm.querySelector('[placeholder="description"]').value)

  let res = await fetch('/api/upload-post', {
    method: 'POST',
    body: formData
  })

  prependNewKittenPost(await res.json())
}

function prependNewKittenPost(post) {
  let kittenPost = new KittenPost(post)
  kittenPosts.unshift(kittenPost)
  postDiv.insertAdjacentHTML('afterbegin', kittenPost.render())
}