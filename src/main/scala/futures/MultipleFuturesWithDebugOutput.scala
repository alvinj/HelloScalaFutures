package futures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object MultipleFuturesWithDebugOutput extends App {

    val startTime = currentTime

    // (a) create three futures
    val aaplFuture = getStockPrice("AAPL")
    val amznFuture = getStockPrice("AMZN")
    val googFuture = getStockPrice("GOOG")

    // (b) get a combined result in a for-comprehension
    println(s"before for:        ${deltaTime(startTime)}")
    val result: Future[(Double, Double, Double)] = for {
        aapl <- aaplFuture
        amzn <- amznFuture
        goog <- googFuture
    } yield (aapl, amzn, goog)
    println(s"after for:         ${deltaTime(startTime)}")

    // (c) do whatever you need to do with the result
    println(s"before onComplete: ${deltaTime(startTime)}")
    result.onComplete {
        case Success(x) => {
            sleep(10)
            val tInSuccessCase = deltaTime(startTime)
            println(s"in Success case:   ${tInSuccessCase}")
            println(s"The stock prices are: $x")
        }
        case Failure(e) => e.printStackTrace
    }
    println(s"after onComplete:  ${deltaTime(startTime)}")

    // important for a little parallel demo: need to keep
    // the jvm’s main thread alive
    println(s"start sleep(5000): ${deltaTime(startTime)}")
    sleep(5000)

    println("after sleep")

    def sleep(time: Long): Unit = Thread.sleep(time)

    // okay, it's not really a web service
    def getStockPrice(stockSymbol: String): Future[Double] = Future {
        val r = scala.util.Random
        val randomSleepTime = r.nextInt(3000)
        val randomPrice = r.nextDouble * 1000
        sleep(randomSleepTime)
        randomPrice
    }

    def currentTime = System.currentTimeMillis()
    def deltaTime(t0: Long) = System.currentTimeMillis() - t0


}
