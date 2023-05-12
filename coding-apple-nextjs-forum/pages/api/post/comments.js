import {connectDB} from "@/util/database";
import {ObjectId} from "mongodb";

export default async function Comments(request, response) {
  if (request.method == 'GET') {
    const db = (await connectDB).db('forum');
    const result = await db.collection('comment').find({parentId: new ObjectId(request.query.postId)}).toArray();

    console.log('server ' + result);
    return response.status(200).json(result);
  }

  return response.status(500).json('internal error');
}
