package com.jft.market.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jft.market.api.ws.CategoryWS;
import com.jft.market.exceptions.ExceptionConstants;
import com.jft.market.model.Category;
import com.jft.market.repository.CategoryRepository;
import com.jft.market.util.Preconditions;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	@Transactional
	public void createCategory(CategoryWS categoryWS) {
		Category category = findCategoryByName(categoryWS.getName());
		if (category == null || !category.getName().equals(categoryWS.getName())) {
			Category category1 = convertWsToEntity(categoryWS);
			saveCategory(category1);
		}
	}

	@Override
	public Category convertWsToEntity(CategoryWS categoryWS) {
		Preconditions.check(categoryWS == null, ExceptionConstants.CATEGORY_NOT_FOUND);
		Category category = new Category();
		category.setName(categoryWS.getName());
		category.setDescription(categoryWS.getDescription());
		return category;
	}

	@Override
	@Transactional
	public void saveCategory(Category category) {
		if (StringUtils.isEmpty(category.getUuid())) {
			category.setUuid(UUID.randomUUID().toString());
		}
		categoryRepository.save(category);
	}

	@Override
	@Transactional
	public Category readCategoryByUuid(String uuid) {
		Category category = categoryRepository.findByUuid(uuid);
		Preconditions.check(category == null, ExceptionConstants.CATEGORY_NOT_FOUND);
		return category;
	}

	@Override
	public CategoryWS readCategory(String uuid) {
		Category category = readCategoryByUuid(uuid);
		Preconditions.check(category == null, ExceptionConstants.CATEGORY_NOT_FOUND);
		Preconditions.check(category.getDeleted().equals(Boolean.TRUE), ExceptionConstants.CATEGORY_IS_DELETED);
		return convertEntityToWS(category);
	}

	@Override
	public CategoryWS convertEntityToWS(Category category) {
		Preconditions.check(category == null, ExceptionConstants.CATEGORY_NOT_FOUND);
		CategoryWS categoryWS = new CategoryWS();
		categoryWS.setName(category.getName());
		categoryWS.setUuid(category.getUuid());
		categoryWS.setDescription(category.getDescription());
		return categoryWS;
	}

	@Override
	@Transactional
	public List<CategoryWS> readCategoriesWS() {
		List<Category> categories = readCategories();
		return convertEntityListToWSList(categories);

	}

	@Override
	@Transactional
	public List<Category> readCategories() {
		//List<Category> categories = categoryRepository.findAll();
		List<Category> categories = categoryRepository.findAllByDeleted(Boolean.FALSE);
		Preconditions.check(categories == null, ExceptionConstants.CATEGORY_NOT_FOUND);
		return categories;
	}

	@Override
	public List<CategoryWS> convertEntityListToWSList(List<Category> categories) {
		Preconditions.check(categories == null, ExceptionConstants.NO_CATEGORY_FOUND);
		List<CategoryWS> categoryWSList = new ArrayList<>();
		categories.forEach(category -> {
			if (category.getDeleted().equals(Boolean.FALSE)) {
				CategoryWS categoryWS = new CategoryWS();
				categoryWS.setName(category.getName());
				categoryWS.setDescription(category.getDescription());
				categoryWS.setUuid(category.getUuid());
				categoryWSList.add(categoryWS);
			}
		});
		return categoryWSList;
	}

	@Override
	@Transactional
	public Category findCategoryByName(String name) {
		return categoryRepository.findByname(name);
	}

	@Override
	@Transactional
	public void updateCategory(CategoryWS categoryWS, String uuid) {
		Category category = categoryRepository.findByUuid(uuid);
		Preconditions.check(category == null, ExceptionConstants.CATEGORY_NOT_FOUND);
		category.setName(categoryWS.getName());
		category.setDescription(categoryWS.getDescription());
		saveCategory(category);
	}

	@Override
	@Transactional
	public void deleteCategory(String uuid) {
		Category category = readCategoryByUuid(uuid);
		Preconditions.check(category == null, ExceptionConstants.CATEGORY_NOT_FOUND);
		Preconditions.check(category.getDeleted().equals(Boolean.TRUE), ExceptionConstants.CATEGORY_ALREADY_DELETED);
		category.setDeleted(Boolean.TRUE);
		category.setEnabled(Boolean.FALSE);
		saveCategory(category);
	}
}
