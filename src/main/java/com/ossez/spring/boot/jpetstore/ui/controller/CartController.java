/*
 *    Copyright 2016-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.ossez.spring.boot.jpetstore.ui.controller;

import com.ossez.spring.boot.jpetstore.component.message.Messages;
import com.ossez.spring.boot.jpetstore.service.CatalogService;
import com.ossez.spring.boot.jpetstore.ui.CartItem;
import com.ossez.spring.boot.jpetstore.domain.Item;
import com.ossez.spring.boot.jpetstore.ui.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kazuki Shimizu
 */
@RequestMapping("/cart")
@Controller
@RequiredArgsConstructor
public class CartController {

	private final CatalogService catalogService;
	private final Cart cart;

	@ModelAttribute
	public CartUpdateForm setUpForm() {
		CartUpdateForm form = new CartUpdateForm();
		for (CartItem item : cart.getCartItems()) {
			form.addItemLine(item.getItem().getItemId(), item.getQuantity());
		}
		return form;
	}

	@GetMapping
	public String viewCart() {
		return "cart/cart";
	}

	@GetMapping(params = "add")
	public String addCartItem(@RequestParam String itemId) {
		if (cart.containsByItemId(itemId)) {
			cart.incrementQuantityByItemId(itemId);
		} else {
			boolean isInStock = catalogService.isItemInStock(itemId);
			Item item = catalogService.getItem(itemId);
			cart.addItem(item, isInStock);
		}
		return "redirect:/cart";
	}

	@GetMapping(params = "remove")
	public String removeCartItem(@RequestParam String itemId) {
		cart.removeItemById(itemId);
		return "redirect:/cart";
	}

	@PostMapping(params = "update")
	public String updateCart(@Validated CartUpdateForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute(
					new Messages().error("Input values are invalid. Please confirm error messages."));
			return viewCart();
		}
		form.getLines().forEach(x -> {
			if (x.getQuantity() > 0) {
				cart.setQuantityByItemId(x.getItemId(), x.getQuantity());
			} else {
				cart.removeItemById(x.getItemId());
			}
		});
		return "redirect:/cart";
	}

}
