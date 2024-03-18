package com.javatechie.crud.example.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.javatechie.crud.example.entity.Employee;
import com.javatechie.crud.example.entity.Product;
import com.javatechie.crud.example.service.ProductService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;



@RestController
@RequestMapping("/product/api")
public class ProductController {

    @Autowired
    private ProductService service;
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public ProductController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody Product product) {
        return service.saveProduct(product);
    }

    @PostMapping("/addProducts")
    public List<Product> addProducts(@RequestBody List<Product> products) {
        return service.saveProducts(products);
    }

    @GetMapping("/products")
    public List<Product> findAllProducts() {
    	
        return service.getProducts();
    }
    
    @GetMapping("/productsemp")
//    @CircuitBreaker(name = "products-employee", fallbackMethod = "processDefaultEmployee")
//    @CircuitBreaker(name = "productsWithEmployee", fallbackMethod = "processDefaultEmployee")
    @HystrixCommand(fallbackMethod="processDefaultEmployee")
    public Map findAllProductsEmployee() {
    	Map map = new HashMap();
//    	// Define the URL of the resource you want to access with port
//        String url = "http://localhost:8085/employee/api/employees";
        			  
        
        // Define the URL of the resource you want to access with eureka service name
        String url = "http://EMPLOYEE-SERVICE/employee/api/employees";

        // Make a GET request using RestTemplate
        ResponseEntity<List<Employee>> response = restTemplate.exchange(
        	      url,
        	      HttpMethod.GET,
        	      null,
        	      new ParameterizedTypeReference<List<Employee>>(){});
        	      List<Employee> employees = response.getBody();
        	     
        	      map.put("Products", service.getProducts());
      	          map.put("Employees", employees);
        	      
        return map;
    }
    
    public Map processDefaultEmployee() {
    	System.out.println("=======================default employes=========================");
    	Map map = new HashMap();
    	map.put("Products", service.getProducts());
    	Employee employee = new Employee("D Vinay", "D varma", "Dummy@gmail.com");
    	Employee employee1 = new Employee("D Vinay1", "D varma1", "Dummy1@gmail.com");
    	Employee employee2 = new Employee("D Vinay2", "D varma2", "Dummy2@gmail.com");
    	Employee employee3 = new Employee("D Vinay3", "D varma3", "Dummy3@gmail.com");
    	List<Employee> empList = new ArrayList<>();
    	empList.add(employee);
    	empList.add(employee1);
    	empList.add(employee2);
    	empList.add(employee3);
        map.put("Employees", empList);
    	return map;
    }

    @GetMapping("/productById/{id}")
    public Product findProductById(@PathVariable int id) {
        return service.getProductById(id);
    }

    @GetMapping("/product/{name}")
    public Product findProductByName(@PathVariable String name) {
        return service.getProductByName(name);
    }

    @PutMapping("/update")
    public Product updateProduct(@RequestBody Product product) {
        return service.updateProduct(product);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id) {
        return service.deleteProduct(id);
    }
}
