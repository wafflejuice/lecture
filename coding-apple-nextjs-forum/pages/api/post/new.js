import { connectDB } from "@/util/database";

export default async function NewPost(request, response) {
  if (request.method == 'POST') {
    if (request.body.title == '') {
      return response.status(400).json('no title');
    }

    const db = (await connectDB).db('forum');
    await db.collection('post').insertOne({
      title: request.body.title,
      content: request.body.content,
    });
  }

  return response.status(200).redirect('/list');
}
