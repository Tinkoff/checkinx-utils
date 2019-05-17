# CheckInx Utils

You can check your query execution plan really simple by using this utils. Your tests could be look like:

```kotlin
class DbIntensiveIntegrationTests : AbstractIntegrationTest() {
    @Autowired
    private lateinit var executionPlanQuery: ExecutionPlanQuery
    @Autowired
    private lateinit var executionPlanParser: ExecutionPlanParser
    @Autowired
    private lateinit var checkInxAssertService: CheckInxAssertService
    @Autowired
    private lateinit var sqlInterceptor: SqlInterceptor
    
    // If you want to get truthful execution plan, generate enough test data
    @Sql("pets.sql") // do it by db dump ...
    @Test
    fun testFindByNameGivenLocationWhenIndexUsingThenCoverageIsHalf() {
       // ARRANGE
       val location = "Moscow"
    
       // ... or generate test data by code
       IntRange(1, 10000).forEach {
           val pet = Pet()
           pet.id = UUID.randomUUID()
           pet.age = it
           pet.location = "Saint Petersburg"
           pet.name = "Jack-$it"
    
           repository.save(pet)
       }
    
       // ACT
    
       // After all arrangements start interception of sql statements
       sqlInterceptor.startInterception()
    
       // Your investigation might be here
       val pets = repository.findByLocation(location)
    
       // After all investigating queries finished stop interception
       sqlInterceptor.stopInterception()
    
       // ASSERT
    
       // Here you can check how many queries were executed
       assertEquals(1, sqlInterceptor.statements.size.toLong())
    
       // If you want something spicy, you can parse raw plan on your own ...
       val executionPlan = executionPlanQuery.execute(sqlInterceptor.statements[0])
       assertTrue(executionPlan.isNotEmpty())
    
       // ... or travers plan tree ...
       val plan = executionPlanParser.parse(executionPlan)
       assertNotNull(plan)
    
       val (_, target, coverage) = plan.rootPlanNode
       assertEquals("ix_pets_location", target)
       assertEquals("Index Scan", coverage)
    
       // Now assert coverage is simple like never before ...
       checkInxAssertService.assertCoverage(CoverageLevel.HALF, "ix_pets_location", plan)
    
       // One more thing, it even could be more simple ...
       checkInxAssertService.assertCoverage(CoverageLevel.HALF, "ix_pets_location", sqlInterceptor.statements[0])
    
       // ... or if you just want to prevent "seq scan" for example, without searching concrete index
       checkInxAssertService.assertCoverage(CoverageLevel.HALF, sqlInterceptor.statements[0])
    }
}
```

Look at the [demo repository](https://github.com/dsemyriazhko/checkinx-demo) to find more examples.

## Starting guide

I’m going to publish checkinx in maven repository. Until I’ve done it use jitpack to get artifacts from github.

Firstly, modify your build.gradle:

1. Add new repository
```groovy
repositories {
  // ...
   maven { url 'https://jitpack.io' }
}
```

2. Add new dependency (please checkout for latest release [here](https://github.com/dsemyriazhko/checkinx-utils/releases))
```groovy
dependencies {
// ...
   implementation 'com.github.dsemyriazhko:checkinx-utils:0.1.4'
}
```

Secondly, add beans & BeanPostProcessor to your configuration
```kotlin
@Configuration
open class DbConfig {
    @Profile("test")
    @Bean
    open fun dataSourceWrapperBeanPostProcessor(): DataSourceWrapper {
        return DataSourceWrapper()
    }

    @Profile("test")
    @Bean
    open fun sqlInterceptor(dataSource: DataSource): SqlInterceptor {
        return PostgresInterceptor(dataSource as ProxyDataSource)
    }

    @Profile("test")
    @Bean
    open fun executionPlanParser(): ExecutionPlanParser {
        return PostgresExecutionPlanParser()
    }

    @Profile("test")
    @Bean
    open fun executionPlanQuery(jdbcTemplate: JdbcTemplate): ExecutionPlanQuery {
        return PostgresExecutionPlanQuery(jdbcTemplate)
    }

    @Profile("test")
    @Bean
    open fun checkInxAssertService(query: ExecutionPlanQuery, parser: ExecutionPlanParser): CheckInxAssertService {
        return CheckInxAssertServiceImpl(query, parser)
    }
}
```

Be sure that you are using org.testcontainers. Its DB version and configuration equal your real DB.

Now you are ready to create your first “intensive” integration test.

## Contribution

If you have time and ideas how to improve checkinx, welcome! I’ll be really happy if you decide to join and contribute.

Hope you'll like it. 
