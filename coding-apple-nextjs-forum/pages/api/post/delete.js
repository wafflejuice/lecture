import {connectDB} from "@/util/database";
import {ObjectId} from 'mongodb';

export default async function DeletePost(request, response) {
  if (request.method == 'POST') {
    const db = (await connectDB).db('forum');
    await db.collection('post').deleteOne({_id: new ObjectId(request.body.toString())});
  }

  response.status(200).json('deleted');
}
