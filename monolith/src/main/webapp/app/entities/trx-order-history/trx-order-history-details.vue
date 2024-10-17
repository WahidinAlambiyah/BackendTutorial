<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxOrderHistory">
        <h2 class="jh-entity-heading" data-cy="trxOrderHistoryDetailsHeading">
          <span v-text="t$('monolithApp.trxOrderHistory.detail.title')"></span> {{ trxOrderHistory.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxOrderHistory.previousStatus')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.OrderStatus.' + trxOrderHistory.previousStatus)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderHistory.newStatus')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.OrderStatus.' + trxOrderHistory.newStatus)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxOrderHistory.changeDate')"></span>
          </dt>
          <dd>
            <span v-if="trxOrderHistory.changeDate">{{ formatDateLong(trxOrderHistory.changeDate) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxOrderHistory.id"
          :to="{ name: 'TrxOrderHistoryEdit', params: { trxOrderHistoryId: trxOrderHistory.id } }"
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

<script lang="ts" src="./trx-order-history-details.component.ts"></script>
