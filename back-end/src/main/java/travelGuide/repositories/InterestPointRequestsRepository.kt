package travelGuide.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import travelGuide.collections.InterestPoint
import travelGuide.collections.InterestPointRequest

interface InterestPointRequestsRepository : MongoRepository<InterestPointRequest?, String?>