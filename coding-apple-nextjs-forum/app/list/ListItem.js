'use client'

import Link from "next/link";

export default function ListItem({ result }) {
  return (
    result.map((e, i) => {
      return (
        <div className="list-item" key={i}>
          <Link href={`/detail/${e.id}`}>
            <h4>{e.title}</h4>
          </Link>
          <Link href={`/edit/${e.id}`}>edit</Link>
          <span onClick={() => {
            fetch('/api/post/delete', {
              method: 'POST',
              body: e.id
            }).then(() => { console.log(12342341) })
          }}>delete</span>
          <p>{e.content}</p>
        </div>
      );
    })
  );
}
