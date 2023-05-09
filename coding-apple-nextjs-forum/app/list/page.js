import { connectDB } from "@/util/database"
import Link from "next/link";
import DetailLink from "./DetailLink";

export default async function List() {
  const db = (await connectDB).db('forum')
  let result = await db.collection('post').find().toArray()

  return (
    < div className="list-bg" >
      {
        result.map((e, i) => {
          return (
            <>
              <div className="list-item" key={i}>
                <Link href={`/detail/${e._id}`}>
                  <h4>{e.title}</h4>
                </Link>
                <Link href={`/edit/${e._id}`}>edit</Link>
                <p>{e.content}</p>
                <DetailLink />
              </div>
            </>
          );
        })
      }
    </div >
  )
} 
