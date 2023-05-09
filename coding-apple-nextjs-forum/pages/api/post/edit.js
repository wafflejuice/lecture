import { connectDB } from "@/util/database";
import { ObjectId } from 'mongodb';

export default async function EditPost(request, response) {
  if (request.method == 'POST') {
    const db = (await connectDB).db('forum');

    const editedPost = {
      title: request.body.title,
      content: request.body.content,
    }
    await db.collection('post').updateOne(
      { _id: new ObjectId(request.body.id) },
      { $set: editedPost },
    )
  }

  response.status(200).redirect('/list');
}
