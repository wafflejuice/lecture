export default function Users() {
  return (
    <div className="p-20">
      <h4>sign up</h4>
      <form action="/api/users" method="POST">
        <input name="identification" placeholder="ID"></input>
        <input name="password" placeholder="PW"></input>
        <button type="submit">submit</button>
      </form>
    </div>
  );
}
