import {connectDB} from "@/util/database";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";
import {ObjectId} from "mongodb";

export default async function Comment(request, response) {
  console.log(request.body);
  console.log(request.body.parentId);
  console.log(request.body.content);

  if (request.method == 'POST') {
    let session = await getServerSession(request, response, authOptions)
    const parsedBody = JSON.parse(request.body);
    const db = (await connectDB).db('forum');
    await db.collection('comment').insertOne(
      {
        parentId: new ObjectId(parsedBody.parentId),
        content: parsedBody.content,
        author: session.user.email,
      }
    )

    return response.status(200).json('ok');
  }

  return response.status(500).json('internal error');
}
