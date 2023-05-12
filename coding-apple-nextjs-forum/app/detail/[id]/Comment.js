'use client';

import {useEffect, useState} from "react";

export default function Comment(props) {
  let [comment, setComment] = useState();
  let [comments, setComments] = useState();

  useEffect(() => {
    fetch('/api/post/comments?postId=' + props.parentId.toString(), {
      method: 'GET'
    })
      .then((r) => r.json())
      .then((r) => {
        setComments(r)
        console.log(comments);
      });
  }, []);


  return (
    <div>
      <hr></hr>
      <div>comment list</div>
      {
        comments?.map((comment, idx) => {
            return (
              <div key={idx}>{comment.content}</div>
            )
          }
        )
      }
      <input onChange={(e) => {
        setComment(e.target.value);
      }}/>
      <button onClick={() => {
        console.log('client ' + props.parentId);
        console.log('client ' + comment);
        fetch('/api/post/comment', {
          method: 'POST', body: JSON.stringify({
            parentId: props.parentId,
            content: comment,
          })
        })
      }}>comment
      </button>
    </div>
  );
}
