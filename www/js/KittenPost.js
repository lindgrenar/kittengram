class KittenPost {
  id = 0
  title = ""
  description = ""
  image = ""
  likes = 0
  published = 0

  constructor(post = {}) {
    Object.assign(this, post)
  }

  async like() {
    let liked = JSON.parse(localStorage['liked'] || "[]")
    if (liked.includes(this.id)) return

    let res = await fetch('/api/like-kitten/' + this.id, { method: 'PATCH' })
    res = await res.text()
    this.likes += +res
    liked.push(this.id)
    localStorage['liked'] = JSON.stringify(liked)
  }

  async unlike() {
    let liked = JSON.parse(localStorage['liked'] || "[]")
    if (!liked.includes(this.id)) return

    let res = await fetch('/api/unlike-kitten/' + this.id, { method: 'PATCH' })
    res = await res.text()
    this.likes -= +res
    liked.splice(liked.indexOf(this.id), 1)
    localStorage['liked'] = JSON.stringify(liked)
  }

  render() {
    let liked = JSON.parse(localStorage['liked'] || "[]")
    let hasLiked = liked.includes(this.id)

    return /*html*/`
      <div class="card" data-id="${this.id}">
        <div class="title-bar">
          <img src="${this.image}" alt="${this.title}">
          <h2>${this.title}</h2>
        </div>
        <img src="${this.image}" alt="${this.title}">
        <div class="content">
          <div class="icon-bar">
            <i class='bx ${hasLiked ? 'bxs-heart' : 'bx-heart'}'></i>
            <span class="likes">${this.likes} ${this.likes == 1 ? 'like' : 'likes'}</span>
            <span class="published">${new Date(this.published * 1000).toLocaleString()}</span>
          </div>
          <p class="description">${this.description}</p>
        </div>
      </div>
    `
  }
}

export default KittenPost