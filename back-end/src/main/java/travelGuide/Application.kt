package travelGuide

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import travelGuide.collections.InterestPoint
import travelGuide.collections.TranslationText
import travelGuide.collections.User
import travelGuide.repositories.InterestPointRepository
import travelGuide.repositories.UserRepository

@SpringBootApplication
open class AccessingDataMongodbApplication : CommandLineRunner {

    @Autowired
    private lateinit var interestPointRepository: InterestPointRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @Throws(Exception::class)
    override fun run(vararg args: String) {
//        interestPointRepository.deleteAll()
//        // save a couple of interest points
//        interestPointRepository.save(InterestPoint(
//            location = arrayOf(53.2734, -7.77832031),
//            name = TranslationText("English", "shinto shrine"),
//            subName = TranslationText("English", "test"),
//            descriptions = arrayOf()))
//
//        userRepository.deleteAll()
//        userRepository.save(User(
//            name = "DoomInAJar",
//            defaultLanguage = "English",
//            permissions = arrayOf()))
        // fetch all interest points
//        println("Interest Points found with findAll():")
//        println("-------------------------------")
//        for (point in repository.findAll()) {
//            println(point)
//        }
//        println()
//        // fetch an individual interest point
//        println("Interest Points found with findByLocationNear(Point(Position(53.2734, -7.77832031)), Distance(10.0, Metrics.KILOMETERS)):")
//        println("--------------------------------")
//        println(repository.findByLocationNear(Point(53.2734, -7.77832031), Distance(10.0, Metrics.KILOMETERS)))
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AccessingDataMongodbApplication::class.java, *args)
        }
    }
}