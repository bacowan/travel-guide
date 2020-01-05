package travelGuide

import org.springframework.data.geo.Point
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics

@SpringBootApplication
open class AccessingDataMongodbApplication : CommandLineRunner {

    @Autowired
    private lateinit var repository: InterestPointRepository

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        repository.deleteAll()
        // save a couple of interest points
        repository.save(InterestPoint(name = "shinto shrine", location = arrayOf(53.2734, -7.77832031)))
        // fetch all interest points
        println("Interest Points found with findAll():")
        println("-------------------------------")
        for (point in repository.findAll()) {
            println(point)
        }
        println()
        // fetch an individual interest point
        println("Interest Points found with findByLocationNear(Point(Position(53.2734, -7.77832031)), Distance(10.0, Metrics.KILOMETERS)):")
        println("--------------------------------")
        println(repository.findByLocationNear(Point(53.2734, -7.77832031), Distance(10.0, Metrics.KILOMETERS)))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AccessingDataMongodbApplication::class.java, *args)
        }
    }
}