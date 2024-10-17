<template>
  <div>
    <h2 id="page-heading" data-cy="TrxDeliveryHeading">
      <span v-text="t$('monolithApp.trxDelivery.home.title')" id="trx-delivery-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('monolithApp.trxDelivery.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'TrxDeliveryCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-trx-delivery"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('monolithApp.trxDelivery.home.createLabel')"></span>
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
              v-bind:placeholder="t$('monolithApp.trxDelivery.home.search')"
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
    <div class="alert alert-warning" v-if="!isFetching && trxDeliveries && trxDeliveries.length === 0">
      <span v-text="t$('monolithApp.trxDelivery.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="trxDeliveries && trxDeliveries.length > 0">
      <table class="table table-striped" aria-describedby="trxDeliveries">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('deliveryAddress')">
              <span v-text="t$('monolithApp.trxDelivery.deliveryAddress')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'deliveryAddress'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('deliveryStatus')">
              <span v-text="t$('monolithApp.trxDelivery.deliveryStatus')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'deliveryStatus'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('assignedDriver')">
              <span v-text="t$('monolithApp.trxDelivery.assignedDriver')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'assignedDriver'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('estimatedDeliveryTime')">
              <span v-text="t$('monolithApp.trxDelivery.estimatedDeliveryTime')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'estimatedDeliveryTime'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('driver.id')">
              <span v-text="t$('monolithApp.trxDelivery.driver')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'driver.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('trxOrder.id')">
              <span v-text="t$('monolithApp.trxDelivery.trxOrder')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'trxOrder.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trxDelivery in trxDeliveries" :key="trxDelivery.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TrxDeliveryView', params: { trxDeliveryId: trxDelivery.id } }">{{ trxDelivery.id }}</router-link>
            </td>
            <td>{{ trxDelivery.deliveryAddress }}</td>
            <td v-text="t$('monolithApp.DeliveryStatus.' + trxDelivery.deliveryStatus)"></td>
            <td>{{ trxDelivery.assignedDriver }}</td>
            <td>{{ formatDateShort(trxDelivery.estimatedDeliveryTime) || '' }}</td>
            <td>
              <div v-if="trxDelivery.driver">
                <router-link :to="{ name: 'MstDriverView', params: { mstDriverId: trxDelivery.driver.id } }">{{
                  trxDelivery.driver.id
                }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="trxDelivery.trxOrder">
                <router-link :to="{ name: 'TrxOrderView', params: { trxOrderId: trxDelivery.trxOrder.id } }">{{
                  trxDelivery.trxOrder.id
                }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TrxDeliveryView', params: { trxDeliveryId: trxDelivery.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TrxDeliveryEdit', params: { trxDeliveryId: trxDelivery.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(trxDelivery)"
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
          id="monolithApp.trxDelivery.delete.question"
          data-cy="trxDeliveryDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-trxDelivery-heading" v-text="t$('monolithApp.trxDelivery.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-trxDelivery"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeTrxDelivery()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="trxDeliveries && trxDeliveries.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-delivery.component.ts"></script>
