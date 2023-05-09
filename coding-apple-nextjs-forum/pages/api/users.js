import { connectDB } from "@/util/database";

export default async function Users(request, response) {
  if (request.method = 'POST') {
    const db = (await connectDB).db('forum');
    const collection = await db.collection('user')

    const result = await collection.findOne({ identification: request.body.identification })
    console.log(result);
    if (result != null) {
      return response.status(400).json('already exists');
    }

    collection.insertOne({
      identification: request.body.identification,
      password: request.body.password,
    });

    return response.status(200).json('created');
  }
}
