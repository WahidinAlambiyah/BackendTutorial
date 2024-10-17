<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxStockAlert">
        <h2 class="jh-entity-heading" data-cy="trxStockAlertDetailsHeading">
          <span v-text="t$('monolithApp.trxStockAlert.detail.title')"></span> {{ trxStockAlert.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxStockAlert.alertThreshold')"></span>
          </dt>
          <dd>
            <span>{{ trxStockAlert.alertThreshold }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxStockAlert.currentStock')"></span>
          </dt>
          <dd>
            <span>{{ trxStockAlert.currentStock }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxStockAlert.id"
          :to="{ name: 'TrxStockAlertEdit', params: { trxStockAlertId: trxStockAlert.id } }"
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

<script lang="ts" src="./trx-stock-alert-details.component.ts"></script>
