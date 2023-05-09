export default async function Write() {
  return (
    <div className="p-20">
      <h4>write</h4>
      <form action="/api/post/new" method="POST">
        <input name="title" placeholder="title"></input>
        <input name="content" placeholder="content"></input>
        <button type="submit">submit</button>
      </form>
    </div>
  )
}
