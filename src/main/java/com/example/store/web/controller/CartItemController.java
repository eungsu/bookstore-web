package com.example.store.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.store.annotation.LoginUser;
import com.example.store.paging.Pagable;
import com.example.store.service.CartItemService;
import com.example.store.vo.CartItem;
import com.example.store.vo.OrderItem;
import com.example.store.vo.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/cart")
public class CartItemController {

	private final CartItemService cartItemService;
	
	@GetMapping("/add")
	public String add(@LoginUser User loginUser, @RequestParam("id") int bookId,
			@RequestParam(name = "quantity", required = false, defaultValue = "1") int quantity) {
		
		cartItemService.insertCartItem(bookId, quantity, loginUser);
		
		return "redirect:/cart/list";
	}
	
	@GetMapping("/list")
	public String list(@LoginUser User user, Pagable pagable, Model model) {
		List<CartItem> cartItems = cartItemService.getMyCartItems(pagable, user.getId());
		
		int totalPrice = cartItems.stream().mapToInt(cartItem -> cartItem.getItemPrice()).sum();
		int totalPaymentPrice = cartItems.stream().mapToInt(cartItem -> cartItem.getItemSellPrice()).sum();
		
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("totalDiscountPrice", totalPrice - totalPaymentPrice);
		model.addAttribute("totalPaymentPrice", totalPaymentPrice);
		
		return "cart/list";
	}
	
	@GetMapping("/update")
	public String list(@LoginUser User loginUser, @RequestParam("id") int cartItemId,
			@RequestParam("quantity") int quantity) {
		
		cartItemService.updateCartItem(cartItemId, quantity, loginUser.getId());
		
		return "redirect:/cart/list";
	}
	
	@GetMapping("/delete")
	public String delete(@LoginUser User loginUser, @RequestParam("id") List<Long> cartItemIds) {
		cartItemService.deleteCartItem(cartItemIds, loginUser.getId());
		
		return "redirect:/cart/list";
	}
	
	@GetMapping("/order")
	public String orderform(@RequestParam("id") List<Long> cartItemIds, Model model) {
		List<CartItem> cartItems = cartItemService.getCartItems(cartItemIds);
		
		int totalBookPrice = cartItems.stream().mapToInt(cartItem -> cartItem.getItemPrice()).sum();
		int totalPaymentPrice = cartItems.stream().mapToInt(cartItem -> cartItem.getItemSellPrice()).sum();
		
		model.addAttribute("totalBookPrice", totalBookPrice);
		model.addAttribute("totalDiscountPrice", totalBookPrice - totalPaymentPrice);
		model.addAttribute("totalOrderPrice", totalPaymentPrice);
		model.addAttribute("totalPaymentPrice", totalPaymentPrice);
		
		List<OrderItem> orderItems = cartItems.stream()
				.map(cartItem -> new OrderItem(cartItem.getBook(), cartItem.getBook().getDiscountPrice(), cartItem.getQuantity()))
				.collect(Collectors.toList());
		model.addAttribute("orderItems", orderItems);		
		
		return "/order/form";
	}
}
