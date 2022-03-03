exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))

  const result = event.calculationResult

  return result % 2 == 0
}
