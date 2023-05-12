import {connectDB} from "@/util/database";
import {getServerSession} from "next-auth";
import {authOptions} from "@/pages/api/auth/[...nextauth]";

export default async function NewPost(request, response) {
  let session = await getServerSession(request, response, authOptions)

  if (request.method == 'POST') {
    if (request.body.title == '') {
      return response.status(400).json('no title');
    }

    const db = (await connectDB).db('forum');
    await db.collection('post').insertOne({
      title: request.body.title,
      content: request.body.content,
      author: session.user.email,
    });
  }

  return response.status(200).redirect('/list');
}
