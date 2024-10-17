<template>
  <div>
    <h2 id="page-heading" data-cy="TrxOrderItemHeading">
      <span v-text="t$('monolithApp.trxOrderItem.home.title')" id="trx-order-item-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.trxOrderItem.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TrxOrderItemCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-trx-order-item"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.trxOrderItem.home.createLabel')"></span>
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
              v-bind:placeholder="t$('monolithApp.trxOrderItem.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && trxOrderItems && trxOrderItems.length === 0">
      <span v-text="t$('monolithApp.trxOrderItem.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="trxOrderItems && trxOrderItems.length > 0">
      <table class="table table-striped" aria-describedby="trxOrderItems">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('quantity')">
              <span v-text="t$('monolithApp.trxOrderItem.quantity')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'quantity'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('price')">
              <span v-text="t$('monolithApp.trxOrderItem.price')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'price'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('order.id')">
              <span v-text="t$('monolithApp.trxOrderItem.order')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'order.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('product.id')">
              <span v-text="t$('monolithApp.trxOrderItem.product')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'product.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trxOrderItem in trxOrderItems" :key="trxOrderItem.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TrxOrderItemView', params: { trxOrderItemId: trxOrderItem.id } }">{{
                trxOrderItem.id
              }}</router-link>
            </td>
            <td>{{ trxOrderItem.quantity }}</td>
            <td>{{ trxOrderItem.price }}</td>
            <td>
              <div v-if="trxOrderItem.order">
                <router-link :to="{ name: 'TrxOrderView', params: { trxOrderId: trxOrderItem.order.id } }">{{
                  trxOrderItem.order.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="trxOrderItem.product">
                <router-link :to="{ name: 'MstProductView', params: { mstProductId: trxOrderItem.product.id } }">{{
                  trxOrderItem.product.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TrxOrderItemView', params: { trxOrderItemId: trxOrderItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TrxOrderItemEdit', params: { trxOrderItemId: trxOrderItem.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(trxOrderItem)"
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
        <span
          id="monolithApp.trxOrderItem.delete.question"
          data-cy="trxOrderItemDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-trxOrderItem-heading" v-text="t$('monolithApp.trxOrderItem.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-trxOrderItem"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeTrxOrderItem()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="trxOrderItems && trxOrderItems.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-order-item.component.ts"></script>
