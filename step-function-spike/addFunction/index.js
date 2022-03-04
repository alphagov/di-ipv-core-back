exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))

  const numbers = event.numbers
  const operation = event.operation

  switch(operation) {
    case 'multiply':
      return numbers.reduce((total, n) => total * n, 1)
      break
    case 'add':
      return numbers.reduce((total, n) => total + n, 0)
      break
    case 'square':
      return numbers.reduce((total, n) => total + (n**2), 0)
      break
    default:
      throw new Error(`unknown operation: ${operation}`)
  }
}
