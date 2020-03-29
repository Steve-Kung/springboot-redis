package cn.stevekung;

import cn.stevekung.pojo.Product;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRedisApplicationTests {

	@Autowired
	DataSource dataSource;
	@Test
	public void contextLoads() throws SQLException {
		Connection connection = dataSource.getConnection();
		System.out.println(connection);
		DruidDataSource dataSource = (DruidDataSource) this.dataSource;
		System.out.println(dataSource.getMaxActive());
	}

	/*
	RedisTemplate<Object, Object>模板的存储结果“乱码”了，是因为
	RedisTemplate<Object, Object>模板的默认的各种key-value序列化器
	(keySerializer、valueSerializer、hashKeySerializer、hashValueSerializer)
    均采用的是JdkSerializationRedisSerializer。

	StringRedisTemplate模板的存储结果未乱码，是因为
	StringRedisTemplate模板的默认的各种key-value序列化器
	(keySerializer、valueSerializer、hashKeySerializer、hashValueSerializer)
	均采用的是StringRedisSerializer.UTF_8字符集的StringRedisSerializer序列化器。
	 */

	@Autowired
	RedisTemplate<String, Object> redisTemplate; // value为对象
	@Autowired
	StringRedisTemplate stringRedisTemplate; // value为字符串
	@Test
	public void redisTest(){
		Product product = Product.builder().name("神州").firstPrice(5000).creatTime(new Date()).build();
		redisTemplate.opsForValue().set("productOne", product);
		Object productOne = redisTemplate.opsForValue().get("productOne");
		System.out.println(productOne);

		stringRedisTemplate.opsForValue().set("steve","健");
		String steve = stringRedisTemplate.opsForValue().get("steve");
		System.out.println(steve);
	}

	// 第三种方式 默认的StringRedisTemplate模板使用
	/*
	使用RedisTemplate模板(无论是默认的还是自定义的)后，取出来的是一个Object,
	如果需要转换为我们需要的类型的话，还需要再进行处理，
	那这样还不如直接使用StringRedisTemplate模板，
	存储前与取出后都手动进行对象字符串间转换：

	引入阿里的Fastjosn依赖(以Fastjson为JSON格式化工具)：
	主动将对象转换为json字符串，在使用StringRedisTemplate模板进行存储与读取

	相比起使用自定义的RedisTemplate模板来讲，使用StringRedisTemplate更顺手，
	因为放进去的、取出来的就是json字符串，将json字符串转换为对象更方便。
	Gson虽然再转换速率上没有Fastjson快，但是在转换结果准确性方面却做得非常好

	RedisTemplate与StringRedisTemplate常用方法 :
	//向redis里存入数据
	stringRedisTemplate.opsForValue（）.set（"key"，"value"）；
	//向redis里存入数据并设置缓存时间
	stringRedisTemplate.opsForValue（）.set（"key"，"value"，6e*1e，TimeUnit.SECONDS）；
	//value做-1操作
	stringRedisTemplate.boundValueOps（"key"）.increment（-1）；
	//value做+1操作
	stringRedisTemplate.boundValueOps（"key"）.increment（1）；
	//根据key获取缓存中的value stringRedis Template.opsForValue（）.get（"key"）；
	//根据key获取过期时间
	stringRedisTemplate.getExpire（"key"）；
	/根据key获取过期时间并换算成指定单位
	stringRedisTemplate.getExpire（"key"，TimeUnit.SECONDS）；
	//根据key删除缓存
	stringRedisTemplate.delete（"key"）；
	//检查key是否存在，返回boolean值
	stringRedisTemplate.haskey（"key"）；
	//向指定key中存放set集合
	stringRedisTemplate.opsForset（）.add（"key"，"obj1"，"obj2"，"obj3"）；
	//设置过期时间
	stringRedisTemplate.expire（"key"，1000，TimeUnit.MILLISECONDS）；
	//根据key查看集合中是否存在指定数据
	stringRedisTemplate.opsForset（）.isMember（"key"，"obj"）；
	//根据key获取set集合
	stringRedisTemplate.opsForSet（）.members（"key"）；

	 */
	@Test
	public void originStringRedisTemplateTest() {
		Product product = Product.builder().id(1).name("神州").firstPrice(5000).creatTime(new Date()).build();
		// 将要放入缓存的对象先转换为JSON字符串
		String jsonString = JSON.toJSONString(product);
		// 放入Redis
		stringRedisTemplate.opsForValue().set("productOne", jsonString);
		// 从Redis中获取
		String productOne = stringRedisTemplate.opsForValue().get("productOne");
		// 将获取到的字符串转换为对应的对象
		// 注意:JSON.parseObject此方法进行字符串对象转换时，依赖于实体模型的构造方法;如果
		// 使用了lombok的@Builder注解，那么最好在补一个全参构造，否则此步骤可能出现异常
		Product productFromRedis = JSON.parseObject(productOne, Product.class);
		System.out.println(productFromRedis);
	}
}
