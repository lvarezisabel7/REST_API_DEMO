package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    // El metodo responde a una request del tipo http://localhost:8080/productos?page=0&size=3
    // Si no se especifica page y size entonces que devuleve los productos ordenados por el nombre, por ejemplo
    @GetMapping
    public ResponseEntity<List<Producto>> findAll(
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size) { 

        ResponseEntity<List<Producto>> responseEntity = null;
        Sort sortByName = Sort.by("name"); // criterio de ordenacion q le voy a pasar a la hora de crear el pageable
        List<Producto> productos = new ArrayList<>();

        // Comprobamos si han envidado page y size
        if(page != null && size != null) {

            // Queremos devolver los productos paginados
            Pageable pageable = PageRequest.of(page, size, sortByName);
            Page<Producto> pageProductos = productoService.findAll(pageable);
            productos = pageProductos.getContent();
            // Tenemos que devolver un obj del tipo responseEntity
            // para ello llamamos al constructor
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
        } else {
            // Solo ordenamiento
            productos = productoService.findAll(sortByName);
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
        }
        return responseEntity;
        
    }

    // Metodo que persiste un producto
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveProduct(@Valid @RequestBody Producto producto, BindingResult validationResults) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        // Comprobar si el producto tiene errores
        if(validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> ObjectErrors = validationResults.getAllErrors();

            ObjectErrors.forEach(objectError -> 
                errores.add(objectError.getDefaultMessage()));    
            // El mensaje es lo que hemos puesto en la anotacion en la entidad Producto

            responseAsMap.put("errores", errores);
            responseAsMap.put("Producto Mal Formado", producto);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }

        // No hay errores en el producto, pues a persistir el producto
        try {
            Producto productoPersistido = productoService.save(producto);
            String succesMessage = "El producto se ha persistido exitosamente";
            responseAsMap.put("Success Message", succesMessage);
            responseAsMap.put("Producto Persistido", productoPersistido); // para que el usuario vea que se ha persistido
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            String error = "Error al intentar persistir el producto y la causa mas probable es: " 
                    + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Producto que se ha intentado persistir", producto);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    // Metodo que actualiza un producto cuyo id recibe como parametro
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@Valid @RequestBody Producto producto, 
                        BindingResult validationResults,
                        @PathVariable(name = "id", required = true) Integer idProducto) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        // Comprobar si el producto tiene errores
        if(validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> ObjectErrors = validationResults.getAllErrors();

            ObjectErrors.forEach(objectError -> 
                errores.add(objectError.getDefaultMessage()));    
            // El mensaje es lo que hemos puesto en la anotacion en la entidad Producto

            responseAsMap.put("errores", errores);
            responseAsMap.put("Producto Mal Formado", producto);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;
        }

        // No hay errores en el producto, pues a actualizar el producto
        try {
            producto.setId(idProducto);
            Producto productoActualizado = productoService.save(producto);
            String succesMessage = "El producto se ha actualizado exitosamente";
            responseAsMap.put("Success Message", succesMessage);
            responseAsMap.put("Producto Actualizado", productoActualizado); // para que el usuario vea que se ha persistido
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String error = "Error al intentar actualizar el producto y la causa mas probable es: " 
                    + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Producto que se ha intentado actualizar", producto);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
    

}
