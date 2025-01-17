/*
 * Copyright ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.besu.consensus.qbft.blockcreation;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hyperledger.besu.consensus.common.bft.BftExtraData;
import org.hyperledger.besu.consensus.qbft.QbftExtraDataCodec;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.core.Address;
import org.hyperledger.besu.ethereum.core.MiningParameters;
import org.hyperledger.besu.ethereum.eth.transactions.sorter.AbstractPendingTransactionsSorter;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;

import org.apache.tuweni.bytes.Bytes;
import org.junit.Before;
import org.junit.Test;

public class QbftBlockCreatorFactoryTest {
  private final QbftExtraDataCodec extraDataCodec = new QbftExtraDataCodec();
  private QbftBlockCreatorFactory qbftBlockCreatorFactory;

  @Before
  public void setUp() throws Exception {
    final MiningParameters miningParams = mock(MiningParameters.class);
    when(miningParams.getExtraData()).thenReturn(Bytes.wrap("Qbft tests".getBytes(UTF_8)));

    qbftBlockCreatorFactory =
        new QbftBlockCreatorFactory(
            mock(AbstractPendingTransactionsSorter.class),
            mock(ProtocolContext.class),
            mock(ProtocolSchedule.class),
            miningParams,
            mock(Address.class),
            mock(Address.class),
            extraDataCodec,
            true); // extraDataWithRoundInformationOnly
  }

  @Test
  public void extraDataWithoutValidatorsAndVoteIsCreated() {
    final Bytes encodedExtraData = qbftBlockCreatorFactory.createExtraData(3, null);
    final BftExtraData bftExtraData = extraDataCodec.decodeRaw(encodedExtraData);

    assertThat(bftExtraData.getValidators()).isEmpty();
    assertThat(bftExtraData.getVote()).isEmpty();
    assertThat(bftExtraData.getRound()).isEqualTo(3);
  }
}
