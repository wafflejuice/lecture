import { connectDB } from '@/util/database';

export default async function Home() {
  const client = await connectDB;
  const db = client.db("forum")

  return (
    <div>

    </div>
  )
}
8
