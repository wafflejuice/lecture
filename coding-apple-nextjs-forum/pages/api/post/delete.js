import {connectDB} from "@/util/database";
import {ObjectId} from 'mongodb';
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";

export default async function DeletePost(request, response) {
  if (request.method == 'POST') {
    let session = await getServerSession(request, response, authOptions);
    const db = (await connectDB).db('forum');

    const post = await db.collection('post').findOne({_id: new ObjectId(request.body.toString())});
    if (!session || (post.author && session.user.email != post.author)) {
      response.status(400).json('not the same user');
      return;
    }

    await db.collection('post').deleteOne({_id: new ObjectId(request.body.toString())});
  }

  response.status(200).json('deleted');
}
