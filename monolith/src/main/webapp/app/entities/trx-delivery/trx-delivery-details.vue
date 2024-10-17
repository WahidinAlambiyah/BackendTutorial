<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxDelivery">
        <h2 class="jh-entity-heading" data-cy="trxDeliveryDetailsHeading">
          <span v-text="t$('monolithApp.trxDelivery.detail.title')"></span> {{ trxDelivery.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxDelivery.deliveryAddress')"></span>
          </dt>
          <dd>
            <span>{{ trxDelivery.deliveryAddress }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxDelivery.deliveryStatus')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.DeliveryStatus.' + trxDelivery.deliveryStatus)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxDelivery.assignedDriver')"></span>
          </dt>
          <dd>
            <span>{{ trxDelivery.assignedDriver }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxDelivery.estimatedDeliveryTime')"></span>
          </dt>
          <dd>
            <span v-if="trxDelivery.estimatedDeliveryTime">{{ formatDateLong(trxDelivery.estimatedDeliveryTime) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxDelivery.driver')"></span>
          </dt>
          <dd>
            <div v-if="trxDelivery.driver">
              <router-link :to="{ name: 'MstDriverView', params: { mstDriverId: trxDelivery.driver.id } }">{{
                trxDelivery.driver.id
              }}</router-link>
            </div>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxDelivery.trxOrder')"></span>
          </dt>
          <dd>
            <div v-if="trxDelivery.trxOrder">
              <router-link :to="{ name: 'TrxOrderView', params: { trxOrderId: trxDelivery.trxOrder.id } }">{{
                trxDelivery.trxOrder.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxDelivery.id"
          :to="{ name: 'TrxDeliveryEdit', params: { trxDeliveryId: trxDelivery.id } }"
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

<script lang="ts" src="./trx-delivery-details.component.ts"></script>
