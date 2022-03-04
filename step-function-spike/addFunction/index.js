exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))

  const numbers = event.numbers

  return numbers.reduce((total, n) => total +  n, 0)
}
