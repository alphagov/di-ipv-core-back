exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))

  const body = JSON.parse(event.body)
  const numbers = body.numbers
  const operation = body.operation
  const result = calculateResult(operation, numbers)

  return {
    statusCode: 200,
    body: JSON.stringify({
      result,
      isItBiggerThan10: result > 10,
      isItEven: result % 2 == 0
    })
  }
}

function calculateResult(operation, numbers) {
  switch (operation) {
    case 'add':
      return numbers.reduce((total, n) => {
        return total + n
      }, 0)
    case 'multiply':
      return numbers.reduce((total, n) => {
        return total * n
      }, 1)
    case 'square':
      return numbers.reduce((total, n) => {
        return total + ( n**2 )
      }, 0)
    default:
      throw new Error(`Unknown operation: ${operation}`)
  }
}

