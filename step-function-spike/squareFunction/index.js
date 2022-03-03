exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))
  const A = event.A

  if (!A) {
    throw new Error('Must provide an input number in field "A"')
  }

  return { "result": A ** 2 }
}
