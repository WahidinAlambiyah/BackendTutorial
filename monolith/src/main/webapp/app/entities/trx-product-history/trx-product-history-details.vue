<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxProductHistory">
        <h2 class="jh-entity-heading" data-cy="trxProductHistoryDetailsHeading">
          <span v-text="t$('monolithApp.trxProductHistory.detail.title')"></span> {{ trxProductHistory.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxProductHistory.oldPrice')"></span>
          </dt>
          <dd>
            <span>{{ trxProductHistory.oldPrice }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxProductHistory.newPrice')"></span>
          </dt>
          <dd>
            <span>{{ trxProductHistory.newPrice }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxProductHistory.changeDate')"></span>
          </dt>
          <dd>
            <span v-if="trxProductHistory.changeDate">{{ formatDateLong(trxProductHistory.changeDate) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxProductHistory.id"
          :to="{ name: 'TrxProductHistoryEdit', params: { trxProductHistoryId: trxProductHistory.id } }"
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

<script lang="ts" src="./trx-product-history-details.component.ts"></script>
