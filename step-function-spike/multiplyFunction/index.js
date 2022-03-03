exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))

  const A = event.A
  const B = event.B

  if ( !A || !B ) {
    throw new Error('must provide input of {"A": n, "B": m }')
  }

  return { "A": A * B }
}
