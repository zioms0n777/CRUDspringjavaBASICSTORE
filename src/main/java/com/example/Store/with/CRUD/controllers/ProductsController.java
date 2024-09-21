package com.example.Store.with.CRUD.controllers;


import com.example.Store.with.CRUD.models.Product;
import com.example.Store.with.CRUD.models.ProductDto;
import com.example.Store.with.CRUD.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {

    @Autowired
    private ProductsRepository productsRepository;


    @GetMapping({"", "/"})
    public String showProductList(Model model) {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";

    }


    @GetMapping("/create")
    public String showCreateForm(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String processCreateForm(@Valid @ModelAttribute("productDto") ProductDto productDto, BindingResult result) {

        // Check if the image is provided
        if (productDto.getImage().isEmpty()) {
            result.addError(new FieldError("productDto", "image", "Image is required"));
        }

        // If validation errors, return to form
        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        MultipartFile image = productDto.getImage();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename(); // Unique filename

        try {
            // Define the upload directory
            String uploadDir = "public/images";
            Path uploadPath = Paths.get(uploadDir);

            // Create the directory if it doesn't exist
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Copy the image to the defined path
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

            // Create a new Product object and set its properties
            Product product = new Product();
            product.setName(productDto.getName());
            product.setImage(storageFileName); // Save the file name to the DB
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCreatedAt(createdAt);

            // Save the product to the database
            productsRepository.save(product);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }

        // Redirect to the product list page after saving
        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model, @RequestParam int id) {

        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/products";
        }


        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(
            Model model, @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ) {
        try {
            Product product = productsRepository.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDto.getImage().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImage = Paths.get(uploadDir + productDto.getImage());

                try {
                    Files.delete(oldImage);
                } catch (IOException e) {
                    System.out.println("Failed to delete old image");
                }
                MultipartFile image = productDto.getImage();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImage(storageFileName);
            }

            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            productsRepository.save(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {

        try {
            Product product = productsRepository.findById(id).get();

            Path imagePath = Paths.get("public/images/" + product.getImage());

            try {
                if (Files.exists(imagePath)) {
                    Files.delete(imagePath);
                } else {
                    System.out.println("File does not exist: " + imagePath.toString());
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete file", e);
            }

            productsRepository.delete(product);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/products";
    }

    }

