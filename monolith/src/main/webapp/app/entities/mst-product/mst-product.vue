<template>
  <div>
    <h2 id="page-heading" data-cy="MstProductHeading">
      <span v-text="t$('monolithApp.mstProduct.home.title')" id="mst-product-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.mstProduct.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'MstProductCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-mst-product"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.mstProduct.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <div class="row">
      <div class="col-sm-12">
        <form name="searchForm" class="form-inline" v-on:submit.prevent="search(currentSearch)">
          <div class="input-group w-100 mt-3">
            <input
              type="text"
              class="form-control"
              name="currentSearch"
              id="currentSearch"
              v-bind:placeholder="t$('monolithApp.mstProduct.home.search')"
              v-model="currentSearch"
            />
            <button type="button" id="launch-search" class="btn btn-primary" v-on:click="search(currentSearch)">
              <font-awesome-icon icon="search"></font-awesome-icon>
            </button>
            <button type="button" id="clear-search" class="btn btn-secondary" v-on:click="clear()" v-if="currentSearch">
              <font-awesome-icon icon="trash"></font-awesome-icon>
            </button>
          </div>
        </form>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && mstProducts && mstProducts.length === 0">
      <span v-text="t$('monolithApp.mstProduct.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="mstProducts && mstProducts.length > 0">
      <table class="table table-striped" aria-describedby="mstProducts">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="t$('monolithApp.mstProduct.name')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('description')">
              <span v-text="t$('monolithApp.mstProduct.description')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'description'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('price')">
              <span v-text="t$('monolithApp.mstProduct.price')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'price'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('quantity')">
              <span v-text="t$('monolithApp.mstProduct.quantity')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('barcode')">
              <span v-text="t$('monolithApp.mstProduct.barcode')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'barcode'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('unitSize')">
              <span v-text="t$('monolithApp.mstProduct.unitSize')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'unitSize'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('category.id')">
              <span v-text="t$('monolithApp.mstProduct.category')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'category.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('brand.id')">
              <span v-text="t$('monolithApp.mstProduct.brand')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'brand.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('mstSupplier.id')">
              <span v-text="t$('monolithApp.mstProduct.mstSupplier')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'mstSupplier.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="mstProduct in mstProducts" :key="mstProduct.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'MstProductView', params: { mstProductId: mstProduct.id } }">{{ mstProduct.id }}</router-link>
            </td>
            <td>{{ mstProduct.name }}</td>
            <td>{{ mstProduct.description }}</td>
            <td>{{ mstProduct.price }}</td>
            <td>{{ mstProduct.quantity }}</td>
            <td>{{ mstProduct.barcode }}</td>
            <td>{{ mstProduct.unitSize }}</td>
            <td>
              <div v-if="mstProduct.category">
                <router-link :to="{ name: 'MstCategoryView', params: { mstCategoryId: mstProduct.category.id } }">{{
                  mstProduct.category.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="mstProduct.brand">
                <router-link :to="{ name: 'MstBrandView', params: { mstBrandId: mstProduct.brand.id } }">{{
                  mstProduct.brand.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="mstProduct.mstSupplier">
                <router-link :to="{ name: 'MstSupplierView', params: { mstSupplierId: mstProduct.mstSupplier.id } }">{{
                  mstProduct.mstSupplier.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'MstProductView', params: { mstProductId: mstProduct.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'MstProductEdit', params: { mstProductId: mstProduct.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(mstProduct)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="monolithApp.mstProduct.delete.question" data-cy="mstProductDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-mstProduct-heading" v-text="t$('monolithApp.mstProduct.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-mstProduct"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeMstProduct()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="mstProducts && mstProducts.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mst-product.component.ts"></script>
