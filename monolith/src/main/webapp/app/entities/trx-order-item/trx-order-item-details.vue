<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxOrderItem">
        <h2 class="jh-entity-heading" data-cy="trxOrderItemDetailsHeading">
          <span v-text="t$('monolithApp.trxOrderItem.detail.title')"></span> {{ trxOrderItem.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxOrderItem.quantity')"></span>
          </dt>
          <dd>
            <span>{{ trxOrderItem.quantity }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderItem.price')"></span>
          </dt>
          <dd>
            <span>{{ trxOrderItem.price }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderItem.order')"></span>
          </dt>
          <dd>
            <div v-if="trxOrderItem.order">
              <router-link :to="{ name: 'TrxOrderView', params: { trxOrderId: trxOrderItem.order.id } }">{{
                trxOrderItem.order.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderItem.product')"></span>
          </dt>
          <dd>
            <div v-if="trxOrderItem.product">
              <router-link :to="{ name: 'MstProductView', params: { mstProductId: trxOrderItem.product.id } }">{{
                trxOrderItem.product.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxOrderItem.id"
          :to="{ name: 'TrxOrderItemEdit', params: { trxOrderItemId: trxOrderItem.id } }"
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

<script lang="ts" src="./trx-order-item-details.component.ts"></script>
