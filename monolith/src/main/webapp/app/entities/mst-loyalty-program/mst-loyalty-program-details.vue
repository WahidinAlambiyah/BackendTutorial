<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstLoyaltyProgram">
        <h2 class="jh-entity-heading" data-cy="mstLoyaltyProgramDetailsHeading">
          <span v-text="t$('monolithApp.mstLoyaltyProgram.detail.title')"></span> {{ mstLoyaltyProgram.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstLoyaltyProgram.pointsEarned')"></span>
          </dt>
          <dd>
            <span>{{ mstLoyaltyProgram.pointsEarned }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstLoyaltyProgram.membershipTier')"></span>
          </dt>
          <dd>
            <span>{{ mstLoyaltyProgram.membershipTier }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstLoyaltyProgram.customer')"></span>
          </dt>
          <dd>
            <div v-if="mstLoyaltyProgram.customer">
              <router-link :to="{ name: 'MstCustomerView', params: { mstCustomerId: mstLoyaltyProgram.customer.id } }">{{
                mstLoyaltyProgram.customer.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstLoyaltyProgram.id"
          :to="{ name: 'MstLoyaltyProgramEdit', params: { mstLoyaltyProgramId: mstLoyaltyProgram.id } }"
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

<script lang="ts" src="./mst-loyalty-program-details.component.ts"></script>
