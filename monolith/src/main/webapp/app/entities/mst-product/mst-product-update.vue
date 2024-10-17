<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.mstProduct.home.createOrEditLabel"
          data-cy="MstProductCreateUpdateHeading"
          v-text="t$('monolithApp.mstProduct.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="mstProduct.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="mstProduct.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.name')" for="mst-product-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="mst-product-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.description')" for="mst-product-description"></label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="mst-product-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.price')" for="mst-product-price"></label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="mst-product-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
              required
            />
            <div v-if="v$.price.$anyDirty && v$.price.$invalid">
              <small class="form-text text-danger" v-for="error of v$.price.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.quantity')" for="mst-product-quantity"></label>
            <input
              type="number"
              class="form-control"
              name="quantity"
              id="mst-product-quantity"
              data-cy="quantity"
              :class="{ valid: !v$.quantity.$invalid, invalid: v$.quantity.$invalid }"
              v-model.number="v$.quantity.$model"
              required
            />
            <div v-if="v$.quantity.$anyDirty && v$.quantity.$invalid">
              <small class="form-text text-danger" v-for="error of v$.quantity.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.barcode')" for="mst-product-barcode"></label>
            <input
              type="text"
              class="form-control"
              name="barcode"
              id="mst-product-barcode"
              data-cy="barcode"
              :class="{ valid: !v$.barcode.$invalid, invalid: v$.barcode.$invalid }"
              v-model="v$.barcode.$model"
            />
            <div v-if="v$.barcode.$anyDirty && v$.barcode.$invalid">
              <small class="form-text text-danger" v-for="error of v$.barcode.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.unitSize')" for="mst-product-unitSize"></label>
            <input
              type="text"
              class="form-control"
              name="unitSize"
              id="mst-product-unitSize"
              data-cy="unitSize"
              :class="{ valid: !v$.unitSize.$invalid, invalid: v$.unitSize.$invalid }"
              v-model="v$.unitSize.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.category')" for="mst-product-category"></label>
            <select class="form-control" id="mst-product-category" data-cy="category" name="category" v-model="mstProduct.category">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstProduct.category && mstCategoryOption.id === mstProduct.category.id ? mstProduct.category : mstCategoryOption
                "
                v-for="mstCategoryOption in mstCategories"
                :key="mstCategoryOption.id"
              >
                {{ mstCategoryOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.brand')" for="mst-product-brand"></label>
            <select class="form-control" id="mst-product-brand" data-cy="brand" name="brand" v-model="mstProduct.brand">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="mstProduct.brand && mstBrandOption.id === mstProduct.brand.id ? mstProduct.brand : mstBrandOption"
                v-for="mstBrandOption in mstBrands"
                :key="mstBrandOption.id"
              >
                {{ mstBrandOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.mstProduct.mstSupplier')" for="mst-product-mstSupplier"></label>
            <select
              class="form-control"
              id="mst-product-mstSupplier"
              data-cy="mstSupplier"
              name="mstSupplier"
              v-model="mstProduct.mstSupplier"
            >
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  mstProduct.mstSupplier && mstSupplierOption.id === mstProduct.mstSupplier.id ? mstProduct.mstSupplier : mstSupplierOption
                "
                v-for="mstSupplierOption in mstSuppliers"
                :key="mstSupplierOption.id"
              >
                {{ mstSupplierOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./mst-product-update.component.ts"></script>
