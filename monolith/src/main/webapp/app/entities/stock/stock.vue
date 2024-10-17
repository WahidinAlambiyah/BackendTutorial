<template>
  <div>
    <h2 id="page-heading" data-cy="StockHeading">
      <span v-text="t$('monolithApp.stock.home.title')" id="stock-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.stock.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'StockCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-stock"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.stock.home.createLabel')"></span>
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
              v-bind:placeholder="t$('monolithApp.stock.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && stocks && stocks.length === 0">
      <span v-text="t$('monolithApp.stock.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="stocks && stocks.length > 0">
      <table class="table table-striped" aria-describedby="stocks">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('quantityAvailable')">
              <span v-text="t$('monolithApp.stock.quantityAvailable')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantityAvailable'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('reorderLevel')">
              <span v-text="t$('monolithApp.stock.reorderLevel')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'reorderLevel'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('expiryDate')">
              <span v-text="t$('monolithApp.stock.expiryDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'expiryDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('product.id')">
              <span v-text="t$('monolithApp.stock.product')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'product.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="stock in stocks" :key="stock.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'StockView', params: { stockId: stock.id } }">{{ stock.id }}</router-link>
            </td>
            <td>{{ stock.quantityAvailable }}</td>
            <td>{{ stock.reorderLevel }}</td>
            <td>{{ formatDateShort(stock.expiryDate) || '' }}</td>
            <td>
              <div v-if="stock.product">
                <router-link :to="{ name: 'MstProductView', params: { mstProductId: stock.product.id } }">{{
                  stock.product.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'StockView', params: { stockId: stock.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'StockEdit', params: { stockId: stock.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(stock)"
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
        <span id="monolithApp.stock.delete.question" data-cy="stockDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-stock-heading" v-text="t$('monolithApp.stock.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-stock"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeStock()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="stocks && stocks.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./stock.component.ts"></script>
