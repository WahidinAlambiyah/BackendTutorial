<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxOrderStock">
        <h2 class="jh-entity-heading" data-cy="trxOrderStockDetailsHeading">
          <span v-text="t$('monolithApp.trxOrderStock.detail.title')"></span> {{ trxOrderStock.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxOrderStock.quantityOrdered')"></span>
          </dt>
          <dd>
            <span>{{ trxOrderStock.quantityOrdered }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderStock.orderDate')"></span>
          </dt>
          <dd>
            <span v-if="trxOrderStock.orderDate">{{ formatDateLong(trxOrderStock.orderDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderStock.expectedArrivalDate')"></span>
          </dt>
          <dd>
            <span v-if="trxOrderStock.expectedArrivalDate">{{ formatDateLong(trxOrderStock.expectedArrivalDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderStock.supplier')"></span>
          </dt>
          <dd>
            <div v-if="trxOrderStock.supplier">
              <router-link :to="{ name: 'MstSupplierView', params: { mstSupplierId: trxOrderStock.supplier.id } }">{{
                trxOrderStock.supplier.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxOrderStock.id"
          :to="{ name: 'TrxOrderStockEdit', params: { trxOrderStockId: trxOrderStock.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-order-stock-details.component.ts"></script>
