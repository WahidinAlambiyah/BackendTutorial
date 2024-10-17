<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxOrder">
        <h2 class="jh-entity-heading" data-cy="trxOrderDetailsHeading">
          <span v-text="t$('monolithApp.trxOrder.detail.title')"></span> {{ trxOrder.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxOrder.orderDate')"></span>
          </dt>
          <dd>
            <span v-if="trxOrder.orderDate">{{ formatDateLong(trxOrder.orderDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrder.deliveryDate')"></span>
          </dt>
          <dd>
            <span v-if="trxOrder.deliveryDate">{{ formatDateLong(trxOrder.deliveryDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrder.orderStatus')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.OrderStatus.' + trxOrder.orderStatus)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrder.paymentMethod')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.PaymentMethod.' + trxOrder.paymentMethod)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrder.totalAmount')"></span>
          </dt>
          <dd>
            <span>{{ trxOrder.totalAmount }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrder.mstCustomer')"></span>
          </dt>
          <dd>
            <div v-if="trxOrder.mstCustomer">
              <router-link :to="{ name: 'MstCustomerView', params: { mstCustomerId: trxOrder.mstCustomer.id } }">{{
                trxOrder.mstCustomer.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="trxOrder.id" :to="{ name: 'TrxOrderEdit', params: { trxOrderId: trxOrder.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-order-details.component.ts"></script>
