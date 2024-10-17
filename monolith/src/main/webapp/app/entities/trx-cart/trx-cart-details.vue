<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxCart">
        <h2 class="jh-entity-heading" data-cy="trxCartDetailsHeading">
          <span v-text="t$('monolithApp.trxCart.detail.title')"></span> {{ trxCart.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxCart.totalPrice')"></span>
          </dt>
          <dd>
            <span>{{ trxCart.totalPrice }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxCart.customer')"></span>
          </dt>
          <dd>
            <div v-if="trxCart.customer">
              <router-link :to="{ name: 'MstCustomerView', params: { mstCustomerId: trxCart.customer.id } }">{{
                trxCart.customer.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="trxCart.id" :to="{ name: 'TrxCartEdit', params: { trxCartId: trxCart.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-cart-details.component.ts"></script>
