package travelGuide

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class AccessingDataMongodbApplication : CommandLineRunner {

    @Autowired
    private val repository: InterestPointRepository? = null

    @Throws(Exception::class)
    override fun run(vararg args: String) {
        repository?.deleteAll()
        // save a couple of customers
        repository?.save(InterestPoint("Alice", "Smith"))
        repository?.save(InterestPoint("Bob", "Smith"))
        // fetch all customers
        println("Customers found with findAll():")
        println("-------------------------------")
        for (customer in repository!!.findAll()) {
            println(customer)
        }
        println()
        // fetch an individual customer
        println("Customer found with findByFirstName('Alice'):")
        println("--------------------------------")
        System.out.println(repository?.findByFirstName("Alice"))
        println("Customers found with findByLastName('Smith'):")
        println("--------------------------------")
        for (customer in repository!!.findByLastName("Smith")!!) {
            System.out.println(customer)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(AccessingDataMongodbApplication::class.java, *args)
        }
    }
}