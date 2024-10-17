<template>
  <div>
    <h2 id="page-heading" data-cy="TrxOrderHeading">
      <span v-text="t$('monolithApp.trxOrder.home.title')" id="trx-order-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.trxOrder.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TrxOrderCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-trx-order"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.trxOrder.home.createLabel')"></span>
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
              v-bind:placeholder="t$('monolithApp.trxOrder.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && trxOrders && trxOrders.length === 0">
      <span v-text="t$('monolithApp.trxOrder.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="trxOrders && trxOrders.length > 0">
      <table class="table table-striped" aria-describedby="trxOrders">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('orderDate')">
              <span v-text="t$('monolithApp.trxOrder.orderDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'orderDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('deliveryDate')">
              <span v-text="t$('monolithApp.trxOrder.deliveryDate')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'deliveryDate'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('orderStatus')">
              <span v-text="t$('monolithApp.trxOrder.orderStatus')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'orderStatus'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('paymentMethod')">
              <span v-text="t$('monolithApp.trxOrder.paymentMethod')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'paymentMethod'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('totalAmount')">
              <span v-text="t$('monolithApp.trxOrder.totalAmount')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'totalAmount'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('mstCustomer.id')">
              <span v-text="t$('monolithApp.trxOrder.mstCustomer')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'mstCustomer.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trxOrder in trxOrders" :key="trxOrder.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TrxOrderView', params: { trxOrderId: trxOrder.id } }">{{ trxOrder.id }}</router-link>
            </td>
            <td>{{ formatDateShort(trxOrder.orderDate) || '' }}</td>
            <td>{{ formatDateShort(trxOrder.deliveryDate) || '' }}</td>
            <td v-text="t$('monolithApp.OrderStatus.' + trxOrder.orderStatus)"></td>
            <td v-text="t$('monolithApp.PaymentMethod.' + trxOrder.paymentMethod)"></td>
            <td>{{ trxOrder.totalAmount }}</td>
            <td>
              <div v-if="trxOrder.mstCustomer">
                <router-link :to="{ name: 'MstCustomerView', params: { mstCustomerId: trxOrder.mstCustomer.id } }">{{
                  trxOrder.mstCustomer.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TrxOrderView', params: { trxOrderId: trxOrder.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TrxOrderEdit', params: { trxOrderId: trxOrder.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(trxOrder)"
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
        <span id="monolithApp.trxOrder.delete.question" data-cy="trxOrderDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-trxOrder-heading" v-text="t$('monolithApp.trxOrder.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-trxOrder"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeTrxOrder()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="trxOrders && trxOrders.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-order.component.ts"></script>
