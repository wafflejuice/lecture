export default function CurrentTime(request, response) {
  response.status(200).json(Date());
}
