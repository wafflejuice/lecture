'use client'

import Link from "next/link";

export default function ListItem({ result }) {
  return (
    result.map((item, i) => {
      return (
        <div className="list-item" key={i}>
          <Link href={`/detail/${item._id}`}>
            <h4>{item.title}</h4>
          </Link>
          <Link href={`/edit/${item._id}`}>edit</Link>
          <span onClick={() => {
            fetch('/api/post/delete', {
              method: 'POST',
              body: item._id
            }).then((r) => r.json())
              .then(() => {

              })
          }}>delete</span>
          <p>{item.content}</p>
        </div>
      );
    })
  );
}
