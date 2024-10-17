<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxCoupon">
        <h2 class="jh-entity-heading" data-cy="trxCouponDetailsHeading">
          <span v-text="t$('monolithApp.trxCoupon.detail.title')"></span> {{ trxCoupon.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxCoupon.code')"></span>
          </dt>
          <dd>
            <span>{{ trxCoupon.code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxCoupon.discountAmount')"></span>
          </dt>
          <dd>
            <span>{{ trxCoupon.discountAmount }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxCoupon.validUntil')"></span>
          </dt>
          <dd>
            <span v-if="trxCoupon.validUntil">{{ formatDateLong(trxCoupon.validUntil) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxCoupon.minPurchase')"></span>
          </dt>
          <dd>
            <span>{{ trxCoupon.minPurchase }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxCoupon.id"
          :to="{ name: 'TrxCouponEdit', params: { trxCouponId: trxCoupon.id } }"
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

<script lang="ts" src="./trx-coupon-details.component.ts"></script>
