package com.example.day.controller;

import com.example.day.entity.Product;
import com.example.day.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String showProductList(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product/home";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product()); // Tạo đối tượng Product mới cho biểu mẫu
        return "product/add";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute @Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "product/add";
        }
        productRepository.save(product);
        return "redirect:/";
    }

    @GetMapping("/product/edit/{id}")
    public String showEditProductForm(@PathVariable("id") int id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        model.addAttribute("product", product);
        return "product/update";
    }

    @PostMapping("/product/edit/{id}")
    public String updateProduct(@PathVariable("id") int id, @ModelAttribute @Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            product.setId(id); // Đảm bảo ID không bị mất khi có lỗi
            return "product/update";
        }
        // Kiểm tra tồn tại sản phẩm trước khi cập nhật
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Invalid product id: " + id);
        }
        product.setId(id); // Đặt ID cho sản phẩm để cập nhật
        productRepository.save(product);
        return "redirect:/";
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        // Kiểm tra tồn tại sản phẩm trước khi xóa
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Invalid product id: " + id);
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + id));
        productRepository.delete(product);
        return "redirect:/";
    }
}
