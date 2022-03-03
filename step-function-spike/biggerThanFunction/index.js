exports.handler =  async function(event, context) {
  console.log("EVENT: \n" + JSON.stringify(event, null, 2))

  const threshold = event.threshold || 10
  const calculationResult = event.calculationResult
  return calculationResult > threshold 
}
