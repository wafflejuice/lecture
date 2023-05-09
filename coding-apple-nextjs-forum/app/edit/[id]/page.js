import { connectDB } from '@/util/database';
import { ObjectId } from 'mongodb';

export default async function Edit(props) {
  const db = (await connectDB).db('forum')
  let result = await db.collection('post').findOne({ _id: new ObjectId(props.params.id) })

  return (
    <div className="p-20">
      <h4>edit</h4>
      <form action="/api/post/edit" method="POST">
        <input style={{ display: 'none' }} name="id" defaultValue={props.params.id}></input>
        <input name="title" defaultValue={result.title}></input>
        <input name="content" defaultValue={result.content}></input>
        <button type="submit">submit</button>
      </form>
    </div>
  )
}
