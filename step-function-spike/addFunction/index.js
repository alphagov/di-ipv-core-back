exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))

  const numbers = event.numbers


  if (!numbers) {
    throw new Error('must provide input field "numbers"')
  }

  return numbers.reduce((total, n) => {
    return n + total
  }, 0)
}
